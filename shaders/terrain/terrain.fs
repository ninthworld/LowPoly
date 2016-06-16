#version 400 core

flat in vec3 fragColor;
flat in vec3 surfaceNormal;
in vec3 toLightVector;
in vec4 shadowCoords;
in float visiblity;

out vec4 out_Color;

uniform vec3 lightColor;
uniform mat4 projectionMatrix;
uniform float shadowMapSize;

uniform sampler2D shadowMap;

const int pcfCount = 4;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

void main(void){

    float texelSize = 1.0 / shadowMapSize;
    float total = 0.0;

    for(int x=-pcfCount; x<=pcfCount; x++){
        for(int y=-pcfCount; y<=pcfCount; y++){
            float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
            if(shadowCoords.z > objectNearestLight + 0.002){
                total += 1.0;
            }
        }
    }

    total /= totalTexels;

    float lightFactor = 1.0 - (total * shadowCoords.w);

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);
    float nDotl = dot(unitNormal, unitLightVector);

    //float brightness = max(nDotl * lightFactor, 0.4);
    //vec3 diffuse = brightness * lightColor;
    //out_Color = vec4(fragColor, 1.0) * vec4(diffuse, 1.0);

    float minLight = 0.4;
    float maxLight = 1.0;
    float brightness = min(maxLight, max(nDotl, minLight));

    float tintLevel = 0.6;
    vec3 diffuse = mix(vec3(1 - tintLevel, 0.5, tintLevel), vec3(tintLevel, 0.5, 1 - tintLevel), (brightness - minLight)/(maxLight - minLight));
    out_Color = vec4(fragColor * diffuse * max(lightFactor, 0.8), 1.0);

}