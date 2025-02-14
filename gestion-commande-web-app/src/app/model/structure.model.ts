export interface Structure {
  id: number;
  acronyme: string;
  nom: string;
  idResponsable: number;
  nomResponsable: string;
  budgetAnnuel: number;
  parentLabId: number;
  parentLabNom: string;
  childEquipesIds: number[]; // List of professor IDs
  childEquipesNoms: string[]; // List of professor names
  type: structurestype; // Assuming structurestype is a string enum

  equipeProfIds: number[]; // List of professor IDs
  equipeProfNames: string[]; // List of professor names
}



export enum structurestype {
  LabodeRecherche = 'Laboratoire de Recherche',
  EquipedeRecherche = 'Equipe de Recherche',
  ProjetdeRecherche = 'Projet de Recherche',
  Département ='Département'
}

