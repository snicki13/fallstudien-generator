import { Component, OnInit } from '@angular/core'
import { StudentGroup } from '../../model/StudentGroup'
import { AuthService } from '../../services/auth.service'
import { CaseStudyGeneratorService } from '../../services/case-study-generator.service'
import { ActivatedRoute } from '@angular/router'
import { tap } from 'rxjs/operators'
import { CaseStudy } from '../../model/CaseStudy'

@Component({
  selector: 'app-wrapper',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.css']
})
export class GenerateComponent implements OnInit {
  public groupInfo?: StudentGroup = undefined
  public caseStudies: CaseStudy[] = []

  // eslint-disable-next-line no-useless-constructor
  constructor (
    private auth: AuthService,
    private generator: CaseStudyGeneratorService,
    private route: ActivatedRoute
  ) {}

  ngOnInit (): void {
    this.auth.init(this.route).pipe(
      tap(info => {
        this.groupInfo = info
      })
    ).subscribe()
    this.generator.getCaseStudies().pipe(
      tap(caseStudies => { this.caseStudies = caseStudies })
    ).subscribe()
  }

  generateStudies (excludedStudies: CaseStudy[]) {
    this.generator.generate(excludedStudies).pipe(
      tap(studies => { this.groupInfo!!.caseStudies = studies })
    ).subscribe()
  }
}
