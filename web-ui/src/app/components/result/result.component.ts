import { Component, Input } from '@angular/core'
import { CaseStudy } from '../../model/CaseStudy'
import { StudentGroup } from '../../model/StudentGroup'

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.css']
})
export class ResultComponent {
  @Input() caseStudies: CaseStudy[] = []
  @Input() groupInfo?: StudentGroup
}
