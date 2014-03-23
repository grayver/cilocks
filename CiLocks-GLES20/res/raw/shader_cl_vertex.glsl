#version 100

precision mediump float;

uniform mat4 u_Light; // in view space
uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;

attribute vec4 a_Position;
attribute vec2 a_UV;
attribute vec3 a_Normal;
attribute vec3 a_Tangent;
attribute vec3 a_Bitangent;

varying vec2 v_UV;
varying vec3 v_CenterLight;
varying float v_CenterDistance;
varying vec3 v_RingLight;
varying float v_RingDistance;
varying vec3 v_Camera;


void main()
{
	gl_Position = u_MVPMatrix * a_Position;
	
	mat3 sv_MVMatrix = mat3(u_MVMatrix);
	vec3 sv_Normal = sv_MVMatrix * a_Normal; // normal vector should be normalized
	vec3 sv_Tangent = sv_MVMatrix * a_Tangent; // tangent vector should be normalized
	vec3 sv_Bitangent = sv_MVMatrix * a_Bitangent; // bitangent vector should be normalized
	
	// own transpose
	mat3 sv_TBN = mat3
		(
			vec3(sv_Tangent.x, sv_Bitangent.x, sv_Normal.x),
			vec3(sv_Tangent.y, sv_Bitangent.y, sv_Normal.y),
			vec3(sv_Tangent.z, sv_Bitangent.z, sv_Normal.z)
		);
	
	// compute varyings
	v_UV = a_UV;
	
	// transfer light vectors to tangent space
	vec3 sv_Position = vec3(u_MVMatrix * a_Position);
	mat3 sv_Light = mat3(u_Light);
	
	v_CenterLight = sv_TBN * normalize(sv_Light[0] - sv_Position);
	v_CenterDistance = distance(sv_Light[0], sv_Position);
	
	vec3 sv_Radial = sv_Position - sv_Light[1];
	float sv_Radius = length(u_Light[3].xyz);
	vec3 sv_RingLight = sv_Light[1] + sv_Radius * normalize(sv_Radial - dot(sv_Radial, sv_Light[2]) * sv_Light[2]);
	v_RingLight = sv_TBN * normalize(sv_RingLight - sv_Position);
	v_RingDistance = distance(sv_RingLight, sv_Position);
	
	v_Camera = sv_TBN * normalize(-sv_Position);
}