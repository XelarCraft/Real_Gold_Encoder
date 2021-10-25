package wesa.xelar.realgoldencoder.encodedecode;


public class AliceContextBuilder {
    private AliceContext.Algorithm algorithm = AliceContext.Algorithm.AES;
    private AliceContext.Mode mode = AliceContext.Mode.CTR;
    private AliceContext.Padding padding = AliceContext.Padding.NO_PADDING;
    private AliceContext.KeyLength keyLength = AliceContext.KeyLength.BITS_256;
    private AliceContext.Pbkdf pbkdf = AliceContext.Pbkdf.PBKDF_2_WITH_HMAC_SHA_512;
    private AliceContext.MacAlgorithm macAlgorithm = AliceContext.MacAlgorithm.HMAC_SHA_512;
    private int ivLength = 16;
    private AliceContext.GcmTagLength gcmTagLength = AliceContext.GcmTagLength.BITS_128;
    private int iterations = 10000;

    public AliceContextBuilder setAlgorithm(AliceContext.Algorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }


    public AliceContextBuilder setMode(AliceContext.Mode mode) {
        this.mode = mode;
        return this;
    }


    public AliceContextBuilder setPadding(AliceContext.Padding padding) {
        this.padding = padding;
        return this;
    }


    public AliceContextBuilder setKeyLength(AliceContext.KeyLength keyLength) {
        this.keyLength = keyLength;
        return this;
    }


    public AliceContextBuilder setPbkdf(AliceContext.Pbkdf pbkdf) {
        this.pbkdf = pbkdf;
        return this;
    }


    public AliceContextBuilder setMacAlgorithm(AliceContext.MacAlgorithm macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
        return this;
    }


    public AliceContextBuilder setIvLength(int ivLength) {
        this.ivLength = ivLength;
        return this;
    }


    public AliceContextBuilder setGcmTagLength(AliceContext.GcmTagLength gcmTagLength) {
        this.gcmTagLength = gcmTagLength;
        return this;
    }

    public AliceContextBuilder setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public AliceContext build() {
        return new AliceContext(algorithm, mode, padding, keyLength, pbkdf, macAlgorithm, ivLength, gcmTagLength, iterations);
    }
}