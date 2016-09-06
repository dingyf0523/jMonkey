uniform sampler2D m_Texture;

varying vec2 texCoord;

void main() {
	vec4 srcColor = texture2D(m_Texture, texCoord);
	float lum = 0.27 * srcColor.r + 0.67 * srcColor.g + 0.06 * srcColor.b;
	if (lum > 0.9) {
		gl_FragColor = vec4(srcColor.rgb, 1.);
	} else {
		gl_FragColor = vec4(0., 0., 0., 1.);
	}
}