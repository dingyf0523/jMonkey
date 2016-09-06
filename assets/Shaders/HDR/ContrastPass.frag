uniform sampler2D m_Texture; //color
uniform sampler2D m_HDRTexture;
uniform sampler2D m_LumTex;

varying vec2 texCoord;

float remap(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
	return (value - inputMin) * ((outputMax - outputMin) / (inputMax - inputMin)) + outputMin;
}

void main() {
	vec4 srcColor = texture2D(m_Texture, texCoord);
	vec4 lumColor = texture2D(m_LumTex, vec2(0.5));
	float lum = 0.27 * lumColor.r + 0.67 * lumColor.g + 0.06 * lumColor.b;
//		float normalizedContrast = 2.;
//		float contrast = remap(normalizedContrast, 0.0, 1.0, 0.2 /*min*/, 4.0 /*max*/);
//		vec4 dstColor = vec4((srcColor.rgb - vec3(0.5)) * contrast + vec3(0.5), 1.0);
//		vec4 outColor = clamp(dstColor, 0.0, 1.0);
	if (0.27 * srcColor.r + 0.67 * srcColor.g + 0.06 * srcColor.b > lum) {
		gl_FragColor = vec4(srcColor.rgb, 1.);
	} else {
		gl_FragColor = vec4(0., 0., 0., 1.);
	}
	//	gl_FragColor = srcColor;
	//debug
//	if (texCoord.x < 0.1 && texCoord.y < 0.1) {
//		gl_FragColor = vec4(lum);
//	}
}