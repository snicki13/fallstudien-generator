import { Component, Input, OnInit } from '@angular/core'
import { CaseStudy } from '../../model/CaseStudy'
import { StudentGroup } from '../../model/StudentGroup'

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.css']
})
export class ResultComponent implements OnInit {
  @Input() caseStudies: CaseStudy[] = []
  @Input() groupInfo?: StudentGroup

  ngOnInit (): void {
  }
}
