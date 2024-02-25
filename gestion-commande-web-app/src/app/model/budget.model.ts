import {RubriqueAllocation} from "./rubriqueAllocation.model";

export interface Budget {
  id: number;
  structureId: number;
  budgetYear: number;
  totalAlloue: number;
  totalRestant: number;
  rubriqueAllocations: [RubriqueAllocation];

}

