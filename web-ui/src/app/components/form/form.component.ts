import {Component, Input} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {CaseStudy} from "../../model/CaseStudy";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent {

  @Input() caseStudies: CaseStudy[] = []

  constructor(private fb: FormBuilder) { }

  profileForm = this.fb.group({
    firstName: [''],
    lastName: [''],
    address: [''],
    dob: [''],
    gender: ['']
  });

  onSubmit() {
    console.log('form data is ', this.profileForm.value);
  }

}
