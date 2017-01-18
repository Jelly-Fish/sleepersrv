#version 110

varying vec3 worldNormal;

void main(void) {
  vec4 color = vec4(245.0 / 255.0, 247.0 / 255.0, 237.0 / 255.0, 1.0);
  float d = max(0.0, dot(normalize(worldNormal), vec3(1.0, 0.0, 0.0)));
  color.rgb *= d;
  gl_FragColor = vec4(color.rgb, 1.0);
}

