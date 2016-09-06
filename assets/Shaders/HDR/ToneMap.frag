uniform sampler2D m_Texture;
uniform sampler2D m_LumTex;

varying vec2 texCoord;

vec3 FilmicCurve(vec3 x) {
	const float A = 0.22;
	const float B = 0.30;
	const float C = 0.10;
	const float D = 0.20;
	const float E = 0.01;
	const float F = 0.30;

	return ((x * (A * x + C * B) + D * E) / (x * (A * x + B) + D * F)) - E / F;
}

vec3 ToneMap_Filmic(vec3 color, vec3 whitePoint) {
	return FilmicCurve(color) / FilmicCurve(whitePoint);
}

float remap(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
	return (value - inputMin) * ((outputMax - outputMin) / (inputMax - inputMin)) + outputMin;
}

void main() {
	vec4 color = texture2D(m_Texture, texCoord);
	if (color.a <= 0.) {
		gl_FragColor = color;
		return;
	}
	vec4 lumColor = texture2D(m_LumTex, vec2(0.));
	float gray = clamp(0.27 * lumColor.r + 0.67 * lumColor.g + 0.06 * lumColor.b, 0.1, 1.);
	vec3 toneMapped = ToneMap_Filmic(color.rgb, vec3(gray * 2.0));

	vec4 finalColor = vec4(toneMapped, color.a);
	gl_FragColor = finalColor;
}