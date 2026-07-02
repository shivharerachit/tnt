"""Project routes: list, view, create and team management."""

from fastapi import APIRouter, Depends, status

from ..core.dependencies import get_current_user, require_admin
from ..db.client import get_db
from ..schemas.project import MembersUpdate, ProjectCreate, ProjectOut
from ..services import project_service

router = APIRouter(prefix="/projects", tags=["projects"])


@router.get("", response_model=list[ProjectOut])
def list_projects(db=Depends(get_db), current_user: dict = Depends(get_current_user)):
    return project_service.list_projects(db, current_user)


@router.get("/{project_id}", response_model=ProjectOut)
def get_project(
    project_id: str, db=Depends(get_db), _: dict = Depends(get_current_user)
):
    return project_service.get_project(db, project_id)


@router.post("", response_model=ProjectOut, status_code=status.HTTP_201_CREATED)
def create_project(
    data: ProjectCreate,
    db=Depends(get_db),
    _: dict = Depends(require_admin),
):
    return project_service.create_project(db, data)


@router.put("/{project_id}/members", response_model=ProjectOut)
def update_members(
    project_id: str,
    data: MembersUpdate,
    db=Depends(get_db),
    _: dict = Depends(require_admin),
):
    return project_service.update_members(db, project_id, data.member_ids)
