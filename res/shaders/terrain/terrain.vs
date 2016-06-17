#version 400 core

in vec3 position;
in vec3 color;
in vec3 normal;

out vec3 fragColor;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec4 shadowCoords;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;

uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

const float density = 0.003;
const float gradient = 5.0;
const float transitionDistance = 20.0;

void main(void){
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    shadowCoords = toShadowMapSpace * worldPosition;

    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCam;
    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    toLightVector = lightPosition - worldPosition.xyz;
    fragColor = color;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);

    distance = distance - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);
}