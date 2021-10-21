export interface StudentGroup {
  groupId: number;
  groupName: string;
  token: string;
  numCaseStudies: number;
  numExclusions: number;
  validUntil: Date;
  caseStudies?: number[];
}
