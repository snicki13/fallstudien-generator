import { Component, Input } from '@angular/core'
import { CaseStudy } from '../../model/CaseStudy'
import { StudentGroup } from '../../model/StudentGroup'

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent {
  @Input() caseStudies: CaseStudy[] = []
  @Input() groupInfo: StudentGroup
  map: boolean[] = []

  onSubmit () {
    console.log(this.getChecked())
  }

  isChecked (study: CaseStudy): boolean {
    return this.map[study.number]
  }

  getChecked (): CaseStudy[] {
    return this.caseStudies.filter(study => this.isChecked(study))
  }

  isDisabled (study: CaseStudy): boolean {
    console.log(this.groupInfo)
    return !this.isChecked(study) && this.map.filter(value => value).length >= this.groupInfo.numExclusions
  }
}
