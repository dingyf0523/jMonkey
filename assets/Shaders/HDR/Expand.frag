uniform sampler2D m_Texture;
uniform float m_Scale; // The step size in pixels.

varying vec2 texCoord;

uniform vec2 g_Resolution; // The width of the texture.

void main() {
	vec4 expand = texture2D(m_Texture, texCoord);
	expand = max(expand, texture2D(m_Texture, vec2(gl_FragCoord.x - 1., gl_FragCoord.y)
			/ g_Resolution));
	expand = max(expand, texture2D(m_Texture, vec2(gl_FragCoord.x + 1., gl_FragCoord.y)
			/ g_Resolution));
	expand = max(expand, texture2D(m_Texture, vec2(gl_FragCoord.x, gl_FragCoord.y - 1.)
			/ g_Resolution));
	expand = max(expand, texture2D(m_Texture, vec2(gl_FragCoord.x, gl_FragCoord.y + 1.)
			/ g_Resolution));
	gl_FragColor = expand;
}