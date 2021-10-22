import { CaseStudy } from './CaseStudy'

export class StudentGroup {
  groupId: number;
  groupName: string;
  token: string;
  numCaseStudies: number;
  numExclusions: number;
  validUntil: Date;
  caseStudies: CaseStudy[] = [];
}
