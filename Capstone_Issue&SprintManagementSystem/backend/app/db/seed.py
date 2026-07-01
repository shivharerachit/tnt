from ..constants import USER_ROLE
from ..core.security import hash_password


def seed_demo_data(db) -> None:
    if db.users.count_documents({}) > 1:
        return
    
    member_id = "user_member"
    member_id1 = "user_member1"

    db.users.insert_many(
        [
            {
                "_id": member_id,
                "name": "Alex Member",
                "email": "member@demo.com",
                "passwordHash": hash_password("Member@123"),
                "role": USER_ROLE["MEMBER"],
            },
            {
                "_id": member_id1,
                "name": "Riya Member",
                "email": "member1@demo.com",
                "passwordHash": hash_password("Member@123"),
                "role": USER_ROLE["MEMBER"],
            },
        ]
    )


def seed_admin(db) -> None:
    if db.users.count_documents({}) > 0:
        return

    admin_id = "user_admin"

    db.users.insert_one(
        {
            "_id": admin_id,
            "name": "Admin User",
            "email": "admin@company.com",
            "passwordHash": hash_password("Admin@123"),
            "role": USER_ROLE["ADMIN"],
        }
    )