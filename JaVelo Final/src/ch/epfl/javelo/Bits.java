package ch.epfl.javelo;

/**
 * Contient deux méthodes permettant d'extraire une séquence de bits d'un vecteur de 32 bits
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class Bits {

    private Bits() {}

    /**
     * Permet d'extraire une séquence de bits d'un vecteur de 32 bits, interprétés comme des bits signés
     *
     * @param value
     *          Vecteur de 32 bits
     * @param start
     *          Bit d'index à partir duquel l'extraction commence
     * @param length
     *          Plage de bits extraits
     *
     * @return les bits extraits, interprétés en tant que bits signés
     */

    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start <= Integer.SIZE
                                            && start >= 0
                                            && length <= Integer.SIZE
                                            && length >= 1
                                            && (start + length) <= Integer.SIZE );
        value = value << ( Integer.SIZE - start - length );
        return value >> ( Integer.SIZE - length );
    }

    /**
     * Permet d'extraire une séquence de bits d'un vecteur de 32 bits, interprétés comme des bits non signés
     *
     * @param value
     *          Vecteur de 32 bits
     * @param start
     *          Bit d'index à partir duquel l'extraction commence
     * @param length
     *          Plage de bits extraits
     *
     * @return les bits extraits, interprétés en tant que bits non signés
     */

    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start <= Integer.SIZE
                                            && start >= 0
                                            && length < Integer.SIZE
                                            && length >= 1
                                            && (start + length) <= Integer.SIZE);
        value = value << ( Integer.SIZE - start - length );
        return value >>> ( Integer.SIZE - length );
    }
}
