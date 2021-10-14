import { Injectable } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {map, tap} from "rxjs/operators";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {AccessToken} from "../model/AccessToken";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private accessToken?: string = undefined

  constructor(
    route: ActivatedRoute,
    private http: HttpClient) {
    route.queryParams.pipe(
      map(params => params["accessToken"]),
      tap(token => this.accessToken = token)
    )
  }

  private getGroupInfo(): Observable<AccessToken> {
    return this.http.get<AccessToken>("/api/group-info", {headers: {"access-token": this.accessToken} })
  }
}
