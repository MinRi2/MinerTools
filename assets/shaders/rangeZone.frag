#define HIGHP

#define alpha 0.3
#define width 2.0
uniform sampler2D u_texture;
uniform vec2 u_texsize;
uniform vec2 u_invsize;
uniform float u_time;
uniform float u_dp;
uniform vec2 u_offset;

varying vec2 v_texCoords;

/** From shields.frag */
void main(){
    vec2 T = v_texCoords.xy;
    vec2 coords = (T * u_texsize) + u_offset;

    T += vec2(sin(coords.y / 4.0 + u_time / 50.0), cos(coords.x / 4.0 + u_time / 50.0)) / u_texsize;

    vec4 color = texture2D(u_texture, T);
    vec2 v = u_invsize;

    vec4 maxed = max(max(max(texture2D(u_texture, T + vec2(0, width) * v), texture2D(u_texture, T + vec2(0, -width) * v)), texture2D(u_texture, T + vec2(width, 0) * v)), texture2D(u_texture, T + vec2(-width, 0) * v));

    if(texture2D(u_texture, T).a < 0.5 && maxed.a > 0.5){
        gl_FragColor = vec4(maxed.rgb, maxed.a * 100.0);
    }else{
        if(color.a > 0.0){
            color.a = alpha * step(sin(coords.x / u_dp + coords.y / u_dp + u_time / 10.0), 0.9);
        }

        gl_FragColor = color;
    }
}