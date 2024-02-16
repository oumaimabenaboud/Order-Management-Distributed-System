export interface Structure {
  id: number;
  acronyme: string;
  nom: string;
  idResponsable: number;
  nomResponsable: string;
  budget: number;
  parentLabId: number;
  type: string; // Assuming structurestype is a string enum

  equipe_prof_ids: number[]; // List of professor IDs
  equipe_prof_names: string[]; // List of professor names
}



enum structurestype {
  LabodeRecherche,EquipedeRecherche, ProjetdeRecherche
}