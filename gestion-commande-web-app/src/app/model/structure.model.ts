import {Professeur} from "./professeur.model";


export interface Structure {
  id: number;
  acronyme: string;
  nom: string;
  responsableStructure: ResponsableStructure;
  respoID: number;
  budget: number;
  type: structurestype;
  professeurs?: Professeur[];
}

interface ResponsableStructure {
  id: number;
  structures: Structure[];
  professeur: Professeur; // Assuming you have defined Professeur interface
}

enum structurestype {
  LabodeRecherche,EquipedeRecherche, ProjetdeRecherche
}
