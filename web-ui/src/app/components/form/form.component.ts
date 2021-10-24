import {Component, EventEmitter, Input, Output} from '@angular/core'
import { CaseStudy } from '../../model/CaseStudy'
import { StudentGroup } from '../../model/StudentGroup'
import {FormResult} from "../generate/generate.component";
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {CaseStudyGeneratorService} from "../../services/case-study-generator.service";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent {
  caseStudies: CaseStudy[] = []
  @Input() groupInfo?: StudentGroup
  @Output() excludedStudies: EventEmitter<FormResult> = new EventEmitter<FormResult>()
  form: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private generatorService: CaseStudyGeneratorService
  ) {
    this.form = this.formBuilder.group({
      studies: new FormArray([], this.maxSelectedCheckboxes()),
      confirmationMail: ['', [Validators.required, this.commaSepEmail]]
    })
    generatorService.getCaseStudies().subscribe(studies => {
      this.caseStudies = studies
      this.caseStudies.forEach(() => this.formArray().push(new FormControl(false)))
    })

  }

  onSubmit () {
    const excludedStudies = this.form.value.studies
      .map((checked: boolean, i: number) => checked ? this.caseStudies[i] : null)
      .filter((v: CaseStudy) => v !== null);
    this.excludedStudies.emit(new FormResult(excludedStudies, this.form.controls.confirmationMail.value))
  }

  private formArray(): FormArray {
    return this.form.controls.studies as FormArray
  }

  maxSelectedCheckboxes() {
    const validator: ValidatorFn = (control: AbstractControl) => {
      const formArray = control as FormArray
      const totalSelected = formArray.controls
        // get a list of checkbox values (boolean)
        .map(control => control.value)
        // total up the number of checked checkboxes
        .reduce((prev, next) => next ? prev + next : prev, 0);

      const tooMany = totalSelected <= this.groupInfo?.numExclusions!!

      if (tooMany) {
      } else {
      }
      // if the total is not greater than the minimum, return the error message
      return tooMany ? null : { 'tooManyExcluded': true };
    };

    return validator;
  }

  commaSepEmail = (control: AbstractControl): { [key: string]: any } | null => {
    const emails = control.value.split(',').map((e: string) => e.trim()).filter((e: string) => e.length > 0)
    const forbidden = emails.some((email: string) => Validators.email(new FormControl(email)));
    return forbidden ? { 'confirmationMail': { value: control.value } } : null;
  };


}
