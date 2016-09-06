#import "Common/ShaderLib/MultiSample.glsllib"

uniform COLORTEXTURE m_Texture;

#if __VERSION__ >= 150
in vec2 texCoord;
out vec4 outFragColor;
#else
varying vec2 texCoord;
#endif



void main()
{
    vec4 texVal=texture2D(m_Texture, texCoord);

//    vec4 texVal=vec4(1.0, 0.0, 0.0, 1.0);
    
    #if __VERSION__ >= 150
        outFragColor = texVal;
    #else
        gl_FragColor = texVal;
    #endif
}