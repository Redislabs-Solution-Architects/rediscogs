import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) { }

  suggestArtists(prefix: string): Observable<any> {
    let params = new HttpParams();
    if (prefix != null) {
      params = params.set('prefix', prefix);
    }
    return this.http.get('/suggest-artists', { params });
  }

  searchAlbums(artistId: string, query: string): Observable<any> {
    let params = new HttpParams();
    if (artistId != null) {
      params = params.set('artistId', artistId);
    }
    if (query != null) {
      params = params.set('query', query);
    }
    return this.http.get('/search-albums', { params });
  }
}
