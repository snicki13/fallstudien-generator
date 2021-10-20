import { Injectable } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {concatMap, map, tap} from "rxjs/operators";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AccessToken} from "../model/AccessToken";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private accessToken?: string = undefined

  constructor(
    private http: HttpClient) {}

  public getGroupInfo(route: ActivatedRoute): Observable<AccessToken> {
    return route.queryParamMap.pipe(
      map(params => params.get("accessToken")!!),
      tap(token => this.accessToken = token),
      map(token => new HttpHeaders().set("access-token", token)),
      concatMap(headers => this.http.get<AccessToken>("/api/group-info",{headers: headers, observe: 'response'})),
      map(response => response.body!!)
    );
  }

  public getAccessToken(): string {
    return this.accessToken!!
  }
}
