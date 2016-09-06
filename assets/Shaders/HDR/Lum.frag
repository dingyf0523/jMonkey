uniform sampler2D m_Texture; //color
uniform vec2 m_Resolution;

varying vec2 texCoord;

void main() {
	vec4 color = vec4(0.);
	vec2 fragCoord = floor(texCoord * m_Resolution);
	for (float i = 0.; i < 4.; i++) {
		for (float j = 0.; j < 4.; j++) {
			color += texture2D(m_Texture, (fragCoord + vec2(i, j)) / m_Resolution);
		}
	}
	color /= 16.;
	gl_FragColor = color;
}