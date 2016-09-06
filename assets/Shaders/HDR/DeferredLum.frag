uniform sampler2D m_LumTex;
uniform sampler2D m_LastTexture;

void main() {
	vec2 tc = vec2(0.);
	vec4 last = texture2D(m_LastTexture, tc);
	vec4 now = texture2D(m_LumTex, tc);

	gl_FragColor = mix(last, now, 0.03);
	//
	//	gl_FragColor = now;
	//	gl_FragColor = last - 0.01;
}