import { Component, OnInit } from '@angular/core';
import { SearchService } from '../search.service';
import { Observable } from 'rxjs';
import { ReactiveFormsModule, FormControl, FormsModule } from "@angular/forms";
import {
  map,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  tap
} from "rxjs/operators";

@Component({
  selector: 'app-search-albums',
  templateUrl: './search-albums.component.html',
  styleUrls: ['./search-albums.component.css']
})
export class SearchAlbumsComponent implements OnInit {

  private loading: boolean = false;
  private results: Observable<any>;
  private searchField: FormControl;

  constructor(private searchService: SearchService) { }

  ngOnInit() {
    this.searchField = new FormControl();
    this.results = this.searchField.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      tap(_ => (this.loading = true)),
      switchMap(term => this.searchService.searchAlbums(term)),
      tap(_ => (this.loading = false))
    );
  }

  doSearch(term: string) {
    this.searchService.searchAlbums(term);
  }

}
