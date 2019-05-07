import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  API_URL = '/api/';

  constructor(private http: HttpClient) { }

  suggestArtists(prefix: string): Observable<any> {
    let params = new HttpParams();
    if (prefix != null) {
      params = params.set('prefix', prefix);
    }
    return this.http.get(this.API_URL + 'suggest-artists', { params });
  }

  searchAlbums(artistId: string, query: string): Observable<any> {
    let params = new HttpParams();
    if (artistId != null) {
      params = params.set('artistId', artistId);
    }
    if (query != null) {
      params = params.set('query', query);
    }
    return this.http.get(this.API_URL + 'search-albums', { params });
  }

  likeAlbum(album: any) {
    const options = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    this.http.post(this.API_URL + 'like-album', album, options).subscribe(
      (val) => {
      },
      response => {
          console.log('POST call in error', response);
      },
      () => {
      });
  }
}
