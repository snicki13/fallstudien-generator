import { Injectable } from '@angular/core'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable } from 'rxjs'
import { CaseStudy } from '../model/CaseStudy'
import { AuthService } from './auth.service'
import { filter, map, mergeMap } from 'rxjs/operators'

@Injectable({
  providedIn: 'root'
})
export class CaseStudyGeneratorService {
  // eslint-disable-next-line no-useless-constructor
  constructor (
    private http: HttpClient,
    private auth: AuthService
  ) { }

  public getCaseStudies (): Observable<CaseStudy[]> {
    return this.auth.getAccessToken().pipe(
      filter(token => token !== undefined),
      map(token => new HttpHeaders().set('access-token', token)),
      mergeMap((headers: HttpHeaders) => this.http.get<CaseStudy[]>('/api/case-studies', { headers: headers, observe: 'response' })),
      map(response => response.body!!)
    )
  }

  public generate (excludedStudies: CaseStudy[]): Observable<CaseStudy[]> {
    return this.auth.getAccessToken().pipe(
      filter(token => token !== undefined),
      map(token => new HttpHeaders().set('access-token', token)),
      mergeMap((headers: HttpHeaders) => this.http.post<CaseStudy[]>('/api/generate', excludedStudies,
        { headers: headers, observe: 'response' })),
      map(response => response.body!!)
    )
  }
}
