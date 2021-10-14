import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CaseStudy} from "../model/CaseStudy";
import {AuthService} from "./auth.service";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class CaseStudyGeneratorService {

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) { }

  public getCaseStudies(): Observable<CaseStudy[]> {
    const headers = new HttpHeaders().set("access-token", this.auth.getAccessToken())
    return this.http.get<CaseStudy[]>("/api/case-studies", {headers: headers, observe: 'response'}).pipe(
      map(response => response.body!!)
    );
  }
}
