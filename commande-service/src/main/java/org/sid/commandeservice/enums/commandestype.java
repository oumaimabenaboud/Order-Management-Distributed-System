package org.sid.commandeservice.enums;


public enum commandestype {
    EN_COURS,
    PASSE,
    ANNULÉE,
    LIVRÉE;


    @Override
    public String toString() {
        switch (this) {
            case EN_COURS:
                return "En cours";
            case PASSE:
                return "Passée";
            case ANNULÉE:
                return "Annulée";
            case LIVRÉE:
                return "Livrée";
            default:
                throw new IllegalArgumentException();
        }
    }
}