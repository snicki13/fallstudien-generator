import { Injectable } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { concatMap, filter, map, tap } from 'rxjs/operators'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { StudentGroup } from '../model/StudentGroup'
import { Observable, ReplaySubject, Subject } from 'rxjs'

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken?: Subject<string> = new ReplaySubject()
  private groupInfo?: Subject<StudentGroup> = new ReplaySubject()

  // eslint-disable-next-line no-useless-constructor
  constructor (private http: HttpClient) {}

  public init (route: ActivatedRoute): Observable<StudentGroup> {
    return route.queryParamMap.pipe(
      map(params => params.get('accessToken')!!),
      tap(token => { this.accessToken!!.next(token) }),
      map(token => new HttpHeaders().set('access-token', token)),
      concatMap(headers => this.http.get<StudentGroup>('api/group-info', { headers: headers, observe: 'response' })),
      map(response => response.body!!),
      tap(groupInfo => { this.groupInfo!!.next(groupInfo) })
    )
  }

  public getAccessToken (): Observable<string> {
    return this.filterUndefined(this.accessToken!!.asObservable())
  }

  private filterUndefined (obs: Observable<any>): Observable<any> {
    return obs.pipe(
      filter(value => value !== undefined)
    )
  }
}
