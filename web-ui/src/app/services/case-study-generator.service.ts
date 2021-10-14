import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CaseStudyGeneratorService {

  constructor(private http: HttpClient) { }

  public getCaseStudies(): Observable<CaseStudy[]> {
    this.http.get<CaseStudy[]>("/api/case-studies", {headers: {"access-token": this.auth.getAccessToken()} })
  }
}
