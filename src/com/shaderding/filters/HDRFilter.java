package com.shaderding.filters;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

public class HDRFilter extends Filter {

	private int screenWidth;
	private int screenHeight;
	private RenderManager renderManager;

	private Texture2D tex64;
	private Texture2D tex16;
	private Texture2D tex4;
	private Texture2D tex1;
	private Pass lumPass;
	private Pass deferredPass2;
	private Texture whiteBuf;
	private float intensity = 1;
	private boolean withDirtTexture = false;

	public HDRFilter()
	{
	}

	@Override
	protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w,
			int h)
	{
		this.renderManager = renderManager;

		screenWidth = w;
		screenHeight = h;

		postRenderPasses = new ArrayList<Pass>();
		final Material lumMat = new Material(manager, "Shaders/HDR/Lum.j3md");
		lumMat.setVector2("Resolution", new Vector2f(screenWidth / 4, screenHeight / 4));
		lumPass = new Pass() {
			@Override
			public boolean requiresSceneAsTexture()
			{
				return true;
			}

		};

		lumPass.init(renderManager.getRenderer(), screenWidth / 4, screenHeight / 4, Format.RGBA8,
				Format.Depth, 1, lumMat);
		postRenderPasses.add(lumPass);

		tex64 = lum(manager, lumPass.getRenderedTexture(), 64);
		tex16 = lum(manager, tex64, 16);
		tex4 = lum(manager, tex16, 4);
		tex1 = lum(manager, tex4, 1);

		whiteBuf = manager.loadTexture("Textures/whiteBuffer.png");
		// 缓冲延时
		final Material deferredMat1 = new Material(manager, "Shaders/HDR/DeferredLum.j3md");
//		deferredMat1.setTexture("LastTexture", blackBuf);
		Pass deferredPass1 = new Pass() {
			private int count;

			@Override
			public void beforeRender()
			{
				super.beforeRender();
				if (count == 0)
				{
					deferredMat1.setTexture("LastTexture", whiteBuf);
				} else
				{
					deferredMat1.setTexture("LastTexture", deferredPass2.getRenderedTexture());
				}
				count++;
			}
		};
		deferredMat1.setTexture("LumTex", tex1);
		deferredPass1.init(renderManager.getRenderer(), 1, 1, Format.RGBA8, Format.Depth, 1,
				deferredMat1);
		postRenderPasses.add(deferredPass1);

		Material deferredMat2 = new Material(manager, "Shaders/Common/DeferredBuffer.j3md");
		deferredPass2 = new Pass();
		deferredMat2.setTexture("LastTexture", deferredPass1.getRenderedTexture());
		deferredPass2.init(renderManager.getRenderer(), 1, 1, Format.RGBA8, Format.Depth, 1,
				deferredMat2);
		postRenderPasses.add(deferredPass2);

		final Material toneMapMat = new Material(manager, "Shaders/HDR/ToneMap.j3md");
		final Pass toneMapPass = new Pass() {

			@Override
			public boolean requiresSceneAsTexture()
			{
				return true;
			}

		};
		toneMapMat.setTexture("LumTex", deferredPass2.getRenderedTexture());
		toneMapPass.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8,
				Format.Depth, 1, toneMapMat);
		postRenderPasses.add(toneMapPass);

		final Material cutMat = new Material(manager, "Shaders/HDR/CutLum.j3md");
		Pass cutPass = new Pass() {
			@Override
			public void beforeRender()
			{
				cutMat.setTexture("Texture", toneMapPass.getRenderedTexture());
			}
		};
		cutPass.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8, Format.Depth,
				1, cutMat);
		postRenderPasses.add(cutPass);

		Texture2D blurTex1 = gaussianBlur(manager, cutPass.getRenderedTexture(), 2, 2, Format.RGBA8);
		Texture2D blurTex2 = gaussianBlur(manager, blurTex1, 8, 2, Format.RGBA8);
//		Texture2D blurTex3 = gaussianBlur(manager, blurTex2, 8, 2, Format.RGBA8);
//		Texture2D blurTex5 = gaussianBlur(manager, blurTex4, 32, 32);

//		Texture2D hblurTex1 = hGaussianBlur(manager, cutPass.getRenderedTexture(), 1, 1);
//		Texture2D hblurTex2 = hGaussianBlur(manager, hblurTex1, 4, 2);
//		Texture2D hblurTex3 = hGaussianBlur(manager, hblurTex2, 16, 4);
//		Texture2D hblurTex4 = hGaussianBlur(manager, hblurTex3, 32, 8);
//		Texture2D hblurTex5 = hGaussianBlur(manager, hblurTex4, 16, 16);

		material = new Material(manager, "Shaders/HDR/HDRFinal.j3md");
		material.setFloat("Intensity", intensity);
		material.setTexture("LumTex", deferredPass2.getRenderedTexture());
		material.setTexture("BlurTex1", blurTex1);
		material.setTexture("BlurTex2", blurTex2);
//		material.setTexture("BlurTex3", blurTex3);
//		material.setTexture("BlurTex5", blurTex5);
//		material.setTexture("HBlurTex", hblurTex5);
		material.setTexture("ToneMapTexture", toneMapPass.getRenderedTexture());

		if (withDirtTexture)
		{
			Texture2D dirtBlur = gaussianBlur(manager, blurTex2, 16, 8, Format.RGBA32F);
			material.setTexture("Dirt", manager.loadTexture("Textures/HDR/ScreenDirt.png"));
			material.setTexture("DirtBlur", dirtBlur);
		}
//		material.setTexture("DebugTex", dirtBlur);
	}

	private Texture2D lum(AssetManager manager, final Texture texture, float width)
	{
		final Material lumMat = new Material(manager, "Shaders/HDR/Lum.j3md");
		lumMat.getAdditionalRenderState().setWireframe(true);
		lumMat.setVector2("Resolution", Vector2f.UNIT_XY.mult(width));
		Pass lumPass = new Pass() {

			@Override
			public void beforeRender()
			{
				lumMat.setTexture("Texture", texture);
			}
		};
		lumPass.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8, Format.Depth,
				1, lumMat);
		postRenderPasses.add(lumPass);
		return lumPass.getRenderedTexture();
	}

	/**
	 * Adds an additional Gaussian blur (one horizontal and one vertical pass) to the specified texture.
	 * @param manager
	 * @param texture A single mipmap texture here.
	 * @return
	 */
	private Texture2D gaussianBlur(AssetManager manager, final Texture texture, final float downSample,
			final float blurScale, Format format)
	{
		final Material expandMat = new Material(manager, "Shaders/HDR/Expand.j3md");
		final Pass expandPass = new Pass() {
			@Override
			public void beforeRender()
			{
				expandMat.setTexture("Texture", texture);
			}
		};
		expandPass.init(renderManager.getRenderer(), (int) (screenWidth / downSample),
				(int) (screenHeight / downSample), format, Format.Depth, 1, expandMat);
//		expandPass.getRenderedTexture().setMagFilter(Texture.MagFilter.Bilinear);
//		expandPass.getRenderedTexture().setMinFilter(Texture.MinFilter.Trilinear);
		postRenderPasses.add(expandPass);

		Texture2D hblur = hGaussianBlur(manager, expandPass.getRenderedTexture(), downSample, blurScale,
				format);
		Texture2D vblur = vGaussianBlur(manager, hblur, downSample, blurScale, format);
		return vblur;
	}

	private Texture2D hGaussianBlur(AssetManager manager, final Texture texture, final float downSample,
			final float blurScale, Format format)
	{
		// Configure horizontal blur pass.
		final Material hBlurMat = new Material(manager, "Common/MatDefs/Blur/HGaussianBlur.j3md");
		final Pass hBlur = new Pass() {

			@Override
			public void beforeRender()
			{
				hBlurMat.setTexture("Texture", texture);
			}
		};
		hBlurMat.setFloat("Size", screenWidth / downSample);
		hBlurMat.setFloat("Scale", blurScale);
		hBlur.init(renderManager.getRenderer(), (int) (screenWidth / downSample),
				(int) (screenHeight / downSample), format, Format.Depth, 1, hBlurMat);
		hBlur.getRenderedTexture().setMagFilter(Texture.MagFilter.Bilinear);
		hBlur.getRenderedTexture().setMinFilter(Texture.MinFilter.Trilinear);
		postRenderPasses.add(hBlur);
		return hBlur.getRenderedTexture();
	}

	private Texture2D vGaussianBlur(AssetManager manager, final Texture texture, final float downSample,
			final float blurScale, Format format)
	{
		// Configure vertical blur pass.
		final Material vBlurMat = new Material(manager, "Common/MatDefs/Blur/VGaussianBlur.j3md");
		final Pass vBlur = new Pass() {
			@Override
			public void beforeRender()
			{
				vBlurMat.setTexture("Texture", texture);
				vBlurMat.setFloat("Size", screenHeight / downSample);
				vBlurMat.setFloat("Scale", blurScale);
			}
		};
		vBlur.init(renderManager.getRenderer(), (int) (screenWidth / downSample),
				(int) (screenHeight / downSample), format, Format.Depth, 1, vBlurMat);
		vBlur.getRenderedTexture().setMagFilter(Texture.MagFilter.Bilinear);
		vBlur.getRenderedTexture().setMinFilter(Texture.MinFilter.Trilinear);
		postRenderPasses.add(vBlur);
		return vBlur.getRenderedTexture();
	}

	@Override
	protected Material getMaterial()
	{
		return material;
	}

	public float getIntensity()
	{
		return intensity;
	}

	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
		if (material != null)
		{
			material.setFloat("Intensity", intensity);
		}
	}

	public boolean isWithDirtTexture()
	{
		return withDirtTexture;
	}

	public void setWithDirtTexture(boolean withDirtTexture)
	{
		this.withDirtTexture = withDirtTexture;
	}
}
