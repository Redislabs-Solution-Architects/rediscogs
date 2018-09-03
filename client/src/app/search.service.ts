import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) { }

  suggestArtists(prefix: string): Observable<any> {
    let params = new HttpParams().set('prefix', prefix);
    return this.http.get('//localhost:8080/suggest-artists', {params});
  }

  searchAlbums(query: string): Observable<any> {
    let params = new HttpParams().set('query', query);
    return this.http.get('//localhost:8080/search-albums', {params});
  }
}
