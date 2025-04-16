<?php
namespace Hello\World;

use App\Features\ContentPortalApi\Exceptions\InvalidCredentialsException;
use App\Helpers\Services\ResponseHelper;
use Illuminate\Auth\Access\AuthorizationException;
use Illuminate\Database\Eloquent\ModelNotFoundException;
use Illuminate\Database\QueryException;
use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Illuminate\Validation\ValidationException;
use Sentry\Laravel\Integration;
use Symfony\Component\HttpFoundation\Response as ResponseAlias;
use Symfony\Component\HttpKernel\Exception\MethodNotAllowedHttpException;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Throwable;
use TypeError;
use function redirect;


class DummyEmptyHandler extends Something implements OtherInterfaces, SomeOtherInterface {}

class Handler extends ExceptionHandler
{
    /**
     * A list of the exception types that are not reported.
     *
     * @var  array
     */
    const FOREIGN_KEY_VIOLATION_CODE = 1451;
    /**
     * The list of the inputs that are never flashed to the session on validation exceptions.
     *
     * @var array<int, string>
     */
    protected $dontFlash = [
        'current_password',
        'password',
        'password_confirmation',
    ];

    /**
     * Register the exception handling callbacks for the application.
     */
    public function register(): void
    {
        $this->reportable(function (Throwable $e) {
            if (!is_null(env('SENTRY_DSN'))) {
                Integration::captureUnhandledException($e);
            }
        });

        $this->renderable(function (ValidationException $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                return ResponseHelper::unprocessableEntity($exception->errors());
            }
        });

        $this->renderable(function (NotFoundHttpException $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                if ($exception->getPrevious() instanceof ModelNotFoundException) {
                    $modelName = class_basename($exception->getPrevious()->getModel());
                    return ResponseHelper::notFound("$modelName does not exist with the specified key!");
                }
                return ResponseHelper::notFound($exception->getMessage());
            }
        });

        $this->renderable(function (InvalidCredentialsException $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                return ResponseHelper::errorResponse(ResponseAlias::HTTP_UNAUTHORIZED, $exception->getMessage());
            }
        });

        $this->renderable(function (AuthorizationException $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                return ResponseHelper::errorResponse(ResponseAlias::HTTP_FORBIDDEN, $exception->getMessage());
            }
        });

        $this->renderable(function (MethodNotAllowedHttpException $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                return ResponseHelper::methodNotAllowed('The specified method for the request is invalid');
            }
            return redirect()->back();
        });

        $this->renderable(function (TypeError $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                return ResponseHelper::badRequest();
            }
        });

        $this->renderable(function (QueryException $exception, Request $request) {
            Log::error($exception);
            if (ResponseHelper::isApiCall($request)) {
                $errorCode = $exception->errorInfo[1];

                if ($errorCode == self::FOREIGN_KEY_VIOLATION_CODE) {
                    return ResponseHelper::errorResponse('Cannot remove this resource permanently,
                as it is related with any other resource', 409);
                }
                return ResponseHelper::internalError();
            }
        });
    }

    public function theOtherFunction(): void {
        echo("Im another function");
    }

}
