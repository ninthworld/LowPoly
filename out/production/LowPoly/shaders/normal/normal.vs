#version 400 core

in vec3 position;
in vec3 color;
in vec3 normal;

out vec3 fragColor;
out vec3 surfaceNormal;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCam;
    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    fragColor = color;
}