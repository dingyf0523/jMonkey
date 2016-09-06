uniform sampler2D m_Texture; //color
uniform sampler2D m_HDRTexture;

varying vec2 texCoord;

float remap(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
	return (value - inputMin) * ((outputMax - outputMin) / (inputMax - inputMin)) + outputMin;
}

void main() {
	vec4 srcColor = texture2D(m_Texture, texCoord);
	float normalizedContrast = 1.;
	float contrast = remap(normalizedContrast, 0.0, 1.0, 0.2 /*min*/, 4.0 /*max*/);
	vec4 dstColor = vec4((srcColor.rgb - vec3(0.5)) * contrast + vec3(0.5), 1.0);
	gl_FragColor = clamp(dstColor, 0.0, 1.0);
}