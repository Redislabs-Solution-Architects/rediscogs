import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SearchAlbumsComponent } from './search/search.component';
import { FavoriteAlbumsComponent } from './favorites/favorites.component';

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'search'},
  {path: 'search' , component: SearchAlbumsComponent},
  {path: 'likes', component: FavoriteAlbumsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
