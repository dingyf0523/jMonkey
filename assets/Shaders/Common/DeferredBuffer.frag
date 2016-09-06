uniform sampler2D m_LastTexture;

varying vec2 texCoord;

void main() {
	vec4 last = texture2D(m_LastTexture, texCoord);

	gl_FragColor = last;
}