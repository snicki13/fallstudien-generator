import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {CaseStudy} from "../model/CaseStudy";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class CaseStudyGeneratorService {

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) { }

  public getCaseStudies(): Observable<CaseStudy[]> {
    this.http.get<CaseStudy[]>("/api/case-studies", {headers: {"access-token": this.auth.getAccessToken()} })
  }
}
