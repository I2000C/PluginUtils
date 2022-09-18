package me.i2000c.plugin_utils.textures;

import java.io.IOException;

public class URLTextureException extends TextureException{
    public URLTextureException(IOException ex){
        super(ex.getMessage());
    }
}
