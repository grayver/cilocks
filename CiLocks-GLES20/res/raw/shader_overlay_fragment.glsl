#version 100

precision mediump float;

uniform sampler2D u_ColorMap;

varying vec2 v_UV;


void main()
{
	gl_FragColor = vec4(texture2D(u_ColorMap, v_UV).rgb, 0.8);
}