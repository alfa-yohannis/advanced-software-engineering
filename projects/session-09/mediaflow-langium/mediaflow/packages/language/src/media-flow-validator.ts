import type { ValidationChecks } from 'langium';
import type { MediaFlowAstType } from './generated/ast.js';
import type { MediaFlowServices } from './media-flow-module.js';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MediaFlowServices) {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.MediaFlowValidator;
    const checks: ValidationChecks<MediaFlowAstType> = {
        // TODO: Declare validators for your properties
        // See doc : https://langium.org/docs/learn/workflow/create_validations/
        /*
        Element: validator.checkElement
        */
    };
    registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class MediaFlowValidator {

    // TODO: Add logic here for validation checks of properties
    // See doc : https://langium.org/docs/learn/workflow/create_validations/
    /*
    checkElement(element: Element, accept: ValidationAcceptor): void {
        // Always accepts
    }
    */
}
