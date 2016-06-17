#version 400 core

in vec3 fragColor;
in vec3 surfaceNormal;

out vec4 out_Color;

void main(void){
    vec3 normal = normalize(surfaceNormal);
    out_Color = vec4(normal.x * 0.5 + 0.5, normal.y * 0.5 + 0.5, normal.z * 0.5 + 0.5, 1.0);
}