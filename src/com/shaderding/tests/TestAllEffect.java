package com.shaderding.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.shaderding.filters.HDRFilter;

public class TestAllEffect extends SimpleApplication implements ActionListener, AnalogListener {

	public TestAllEffect()
	{
		super();
		AppSettings set = new AppSettings(true);
		set.setResolution(800, 450);
		set.setVSync(true);
//		set.setGammaCorrection(true);
		setSettings(set);
		setShowSettings(false);
	}

	public static void main(String[] args)
	{
		new TestAllEffect().start();
	}

	@Override
	public void simpleInitApp()
	{
//		flyCam.setMoveSpeed(10);
		setPauseOnLostFocus(true);
		setDisplayStatView(false);
		setDisplayFps(false);
		flyCam.setEnabled(false);

		cam.setFrustumPerspective(45, (float) cam.getWidth() / cam.getHeight(), 0.1f, 500);

		// light
		rootNode.addLight(new AmbientLight());
		DirectionalLight dl = new DirectionalLight(new Vector3f(0, -2, 0.5f));
		rootNode.addLight(dl);

		// model
		assetManager.registerLocator("D:/Model/测试场景/sponza", FileLocator.class);
		Spatial scene = assetManager.loadModel("sponza.obj");
		scene.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.attachChild(scene);

		cam.setLocation(new Vector3f(32.352814f, 4.0501027f, 2.131302f));
		cam.setRotation(new Quaternion(0.009862403f, -0.7174862f, 0.010160365f, 0.6964287f));

		// fpp
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		viewPort.addProcessor(fpp);

		DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 4);
		dlsf.setLight(dl);
		dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
		fpp.addFilter(dlsf);

		HDRFilter hdr = new HDRFilter();
		hdr.setIntensity(0.5f);
		hdr.setWithDirtTexture(true);
		fpp.addFilter(hdr);

		FXAAFilter fxaa = new FXAAFilter();
		fpp.addFilter(fxaa);

		// input
		inputManager.addMapping("space", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1));
		inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2));
		inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3));
		inputManager.addMapping("4", new KeyTrigger(KeyInput.KEY_4));
		inputManager.addMapping("5", new KeyTrigger(KeyInput.KEY_5));
		inputManager.addMapping("6", new KeyTrigger(KeyInput.KEY_6));
		inputManager.addListener(this, "space", "1", "2", "3", "4", "5", "6");

		cam.setLocation(new Vector3f(29.64926f, 2.634091f, 1.5967299f));
		cam.setRotation(new Quaternion(-0.01542956f, -0.6956376f, -0.014947932f, 0.71807164f));
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf)
	{
		if (isPressed)
		{
			if ("space".equals(name))
			{
			}
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf)
	{
		if ("1".equals(name))
		{
		}
	}

}
