create table if not exists file_metadata (
                                             id uuid primary key,
                                             bucket text not null,
                                             object_key text not null,
                                             original_name text not null,
                                             content_type text,
                                             size_bytes bigint not null,
                                             created_at timestamptz not null default now()
    );

create index if not exists idx_file_metadata_created_at on file_metadata(created_at);