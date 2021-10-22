import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { GenerateComponent } from './components/generate/generate.component'

const routes: Routes = [
  { path: 'generate', component: GenerateComponent }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
