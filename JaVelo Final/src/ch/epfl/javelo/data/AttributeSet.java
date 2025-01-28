package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * Permet de créer des objets représentant des fonctions mathématiques des réels vers les réels
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public record AttributeSet(long bits) {

    /**
     * Valide les bits qu'il reçoit
     *
     * @param bits
     *          Représente le contenu de l'ensemble d'attributs
     *
     * @throws IllegalArgumentException
     *          si un des bits passés en argument correspond à un attribut non-valide
     */
    public AttributeSet {
        Preconditions.checkArgument(bits >>> Attribute.COUNT == 0);
    }

    /**
     * Crée un ensemble d'attributs à partir des attributs en paramètres
     *
     * @param attributes
     *          Attribut quelconque de l'énumération Attribute
     *
     * @return un ensemble d'attributs contenant uniquement ceux donnés en argument
     */
    public static AttributeSet of(Attribute... attributes) {
        long bits = 0;
        for (Attribute att : attributes)
            bits |= 1L << att.ordinal();
        return new AttributeSet(bits);
    }

    /**
     * Permet de savoir si un attribut appartient à un ensemble
     *
     * @param attribute
     *          Attribut quelconque de l'énumération Attribute
     *
     * @return true si l'attribut est contenu dans this, false sinon
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return (this.bits & mask) == mask;
    }

    /**
     * Permet de savoir si deux ensembles ont au moins attribut en commun
     *
     * @param that
     *          Ensemble d'attribut à comparer avec this
     *
     * @return true si les deux ensembles ont au moins un attribut en commun, false sinon
     */
    public boolean intersects(AttributeSet that) {
        return ( this.bits() & that.bits() ) != 0;
    }

    /**
     * Permet de lister tous les attributs d'un ensemble dans l'ordre dans lequel
     * ils sont dans l'énumération Attribute
     *
     * @return la liste de tous les attributs de l'ensemble this
     */
    @Override
    public String toString () {
        StringJoiner string = new StringJoiner(",","{","}");
        for (Attribute att : Attribute.ALL)
            if ( this.contains(att) ) string.add( att.keyValue() );
        return string.toString();
    }

}