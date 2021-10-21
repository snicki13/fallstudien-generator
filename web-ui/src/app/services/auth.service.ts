import { Injectable } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { concatMap, map, tap } from 'rxjs/operators'
import { Observable } from 'rxjs'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { StudentGroup } from '../model/StudentGroup'

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken?: string = undefined
  private groupInfo?: StudentGroup = undefined

  // eslint-disable-next-line no-useless-constructor
  constructor (private http: HttpClient) {}

  public init (route: ActivatedRoute) {
    return route.queryParamMap.pipe(
      map(params => params.get('accessToken')!!),
      tap(token => { this.accessToken = token }),
      map(token => new HttpHeaders().set('access-token', token)),
      concatMap(headers => this.http.get<StudentGroup>('/api/group-info', { headers: headers, observe: 'response' })),
      map(response => response.body!!),
      tap(groupInfo => { this.groupInfo = groupInfo })
    ).subscribe()
  }

  public getGroupInfo (route: ActivatedRoute): Observable<StudentGroup> {
    return route.queryParamMap.pipe(
      map(params => params.get('accessToken')!!),
      tap(token => { this.accessToken = token }),
      map(token => new HttpHeaders().set('access-token', token)),
      concatMap(headers => this.http.get<StudentGroup>('/api/group-info', { headers: headers, observe: 'response' })),
      map(response => response.body!!)
    )
  }

  public getAccessToken (): string {
    return this.accessToken!!
  }
}
