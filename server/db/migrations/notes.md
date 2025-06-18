# How to write migrations

This is the naming convention of migration files:

```
{version}_{title}.up.{extension}
{version}_{title}.down.{extension}
```

Where `version` is an integer increasing with the database version
(usually just a number of the date), `title` is the name of the 
migration (like create_table, add_column, they are usually in 
snake_case) and `extension` is `sql` (usually).

You always have two files, `up` contains the changes themselves and 
`down` contains the necessary operations to revert said change.

Once you have the necessary files, you can migrate your database 
either through a CLI or through the go library.

## CLI
You can run a migration with the following command:

```sh
migrate -database YOUR_DATABASE_URL -path PATH_TO_YOUR_MIGRATIONS up
```

where `YOUR_DATABASE_URL` is the url to the database, for example
`sqlite://path/to/database.db` or `postgres://localhost:5432/database`.
`PATH_TO_YOUR_MIGRATIONS` is the path to the `migrations` folder.

Here is the `--help` page to the `migrate` tool:

```
Usage: migrate OPTIONS COMMAND [arg...]
       migrate [ -version | -help ]

Options:
  -source          Location of the migrations (driver://url)
  -path            Shorthand for -source=file://path
  -database        Run migrations against this database (driver://url)
  -prefetch N      Number of migrations to load in advance before executing (default 10)
  -lock-timeout N  Allow N seconds to acquire database lock (default 15)
  -verbose         Print verbose logging
  -version         Print version
  -help            Print usage

Commands:
  create [-ext E] [-dir D] [-seq] [-digits N] [-format] [-tz] NAME
           Create a set of timestamped up/down migrations titled NAME, in directory D with extension E.
           Use -seq option to generate sequential up/down migrations with N digits.
           Use -format option to specify a Go time format string. Note: migrations with the same time cause "duplicate migration version" error.
           Use -tz option to specify the timezone that will be used when generating non-sequential migrations (defaults: UTC).

  goto V       Migrate to version V
  up [N]       Apply all or N up migrations
  down [N] [-all]    Apply all or N down migrations
        Use -all to apply all down migrations
  drop [-f]    Drop everything inside database
        Use -f to bypass confirmation
  force V      Set version V but don't run migration (ignores dirty state)
  version      Print current migration version

Source drivers: file
Database drivers: stub, sqlite
```

## Library 
TODO
