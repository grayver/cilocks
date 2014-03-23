#version 100

precision mediump float;

uniform sampler2D u_ColorMap;
uniform sampler2D u_NormalMap;

varying vec2 v_UV;
varying vec3 v_CenterLight;
varying float v_CenterDistance;
varying vec3 v_RingLight;
varying float v_RingDistance;
varying vec3 v_Camera;


void main()
{
	vec3 sv_Normal = normalize(texture2D(u_NormalMap, v_UV).rgb * 2.0 - 1.0);
	vec4 sv_TexColor = texture2D(u_ColorMap, v_UV);
	
	vec3 sv_Ambient = 0.05 * sv_TexColor.rgb;
	
	vec3 sv_CenterDiffuse = dot(sv_Normal, v_CenterLight) * sv_TexColor.rgb;
	vec3 sv_RingDiffuse = dot(sv_Normal, v_RingLight) * sv_TexColor.rgb;
	
	float sv_SpecularCos = dot(v_Camera, reflect(-v_CenterLight, sv_Normal));
	vec3 sv_CenterSpecular = sv_SpecularCos * sv_TexColor.rgb;
	
	const vec3 sv_MatHLColor = vec3(0.800, 0.913, 1.000);
	const float sv_Shininess = 15.0;
	vec3 sv_CenterHL = pow(max(sv_SpecularCos, 0.0), sv_Shininess) * sv_MatHLColor;
	
	float sv_CenterAttenuation = 1.0 / (1.0 + (0.25 * v_CenterDistance * v_CenterDistance));
	float sv_RingAttenuation = 1.0 / (1.0 + (0.25 * v_RingDistance * v_RingDistance));
	
	vec3 sv_FinalColor = max(
			sv_Ambient
			+ sv_CenterAttenuation * (0.2 * sv_CenterDiffuse + sv_CenterSpecular + 0.4 * sv_CenterHL)
			+ 0.7 * sv_RingAttenuation * sv_RingDiffuse,
		sv_Ambient);
	
	gl_FragColor = vec4(sv_FinalColor, sv_TexColor.a);
}