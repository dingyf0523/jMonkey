uniform sampler2D m_Texture; //color
uniform sampler2D m_ToneMapTexture; //color
uniform sampler2D m_LumTex; //color
uniform sampler2D m_BlurTex1;
uniform sampler2D m_BlurTex2;
//uniform sampler2D m_BlurTex3;
//uniform sampler2D m_BlurTex5;
//uniform sampler2D m_HBlurTex;
//uniform sampler2D m_DebugTex;
uniform float m_Intensity;
#ifdef HAS_DIRT
uniform sampler2D m_Dirt;
uniform sampler2D m_DirtBlur;
#endif

varying vec2 texCoord;

void main() {
	vec4 color = texture2D(m_Texture, texCoord);
	vec4 toneMapColor = texture2D(m_ToneMapTexture, texCoord);
	vec4 blurTex1 = texture2D(m_BlurTex1, texCoord);
	vec4 blurTex2 = texture2D(m_BlurTex2, texCoord);
	//	vec4 blurTex3 = texture2D(m_BlurTex3, texCoord);
	//	vec4 blurTex5 = texture2D(m_BlurTex5, texCoord);
	//	vec4 hBlurTex = texture2D(m_HBlurTex, texCoord);
	//	vec4 debugTex = texture2D(m_DebugTex, texCoord);
	/*gl_FragColor = color + (blurTex1 + blurTex2 + blurTex3 + blurTex4) * 0.15*/;

	//	float lum = 0.27 * toneMapColor.r + 0.67 * toneMapColor.g + 0.06 * toneMapColor.b;

	vec4 lumColor = texture2D(m_LumTex, vec2(0.));
	float gray = clamp(0.27 * lumColor.r + 0.67 * lumColor.g + 0.06 * lumColor.b, 0.0, 1.);
	float intensity = clamp(m_Intensity, 0., 1.);
	gl_FragColor = mix(toneMapColor, color, gray * (1. - intensity)) + (blurTex1 + blurTex2)
			* intensity * 0.2;

#ifdef HAS_DIRT
	vec4 dirt = texture2D(m_Dirt, texCoord);
	vec4 dirtBlur = texture2D(m_DirtBlur, texCoord);
	gl_FragColor += dirt*pow(dirtBlur,vec4(0.4));
#endif

	//	if (texCoord.x < 0.2 && texCoord.y < 0.2) {
	//		gl_FragColor = texture2D(m_DebugTex, texCoord / 0.2);
	//	}
}