import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { HomeComponent } from './pages/home/home.component';
import { GrupoDetalleComponent } from './pages/grupo-detalle/grupo-detalle.component';
import { CrearGrupoComponent } from './pages/crear-grupo/crear-grupo.component';
import { FriendsListComponent } from './pages/friends-list/friends-list.component';
import { GastoDetalleComponent } from './pages/gasto-detalle/gasto-detalle.component';
import { EditarGastoComponent } from './pages/editar-gasto/editar-gasto.component';
import { CrearGastoComponent } from './pages/crear-gasto/crear-gasto.component';
import { GrupoParejaComponent } from './pages/grupo-pareja/grupo-pareja.component';

export const routes: Routes = [
    { path: "home", component: HomeComponent },
    { path: "login", component: LoginComponent },
    { path: "register", component: RegisterComponent },
    { path: "grupo/:id/detalle", component: GrupoDetalleComponent },
    { path: "group-duo/:id", component: GrupoParejaComponent },
    { path: "gasto/:id/detalle", component: GastoDetalleComponent },
    { path: "crear-grupo", component: CrearGrupoComponent },
    { path: "amigos", component: FriendsListComponent },
    { path: 'editar-gasto', component: EditarGastoComponent },
    { path: 'crear-gasto', component: CrearGastoComponent },
    { path: "**", redirectTo: "login", pathMatch: "full" },

];
